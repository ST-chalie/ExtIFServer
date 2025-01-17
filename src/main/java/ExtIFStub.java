import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.invoke.MethodHandles;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ExtIFStub extends HttpServlet {
    static Logger logger;
    final static String TID = java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
    private static Properties properties = new Properties();

    public static void main(String[] args) {
        try {
            properties.load(
                    new FileInputStream("./ExtIFStub.properties"));

            //System.setProperty("date", new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime()));
            ThreadContext.put("tid", TID);
            logger = LogManager.getLogger(MethodHandles.lookup().lookupClass());
            run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void run() throws MalformedURLException {
        // ファイル読み込み
      /*  final ResourceBundle rb = ResourceBundle.getBundle("ExtIFStub", Locale.getDefault(),
                new URLClassLoader(new URL[]{Paths.get("./").toFile().toURI().toURL()}));*/
        ServletContextHandler handler = new ServletContextHandler();

        final ServletHolder holder = new ServletHolder(new ExtIFStub());
        final HandlerList handlerList = new HandlerList();

        Server server = new Server(Integer.parseInt(properties.getProperty("fromRequestPortNumber")));
        // URI を追加(ハンドラに追加
        handler.addServlet(holder, "/");
        // ハンドラハンドラリストに設定
        handlerList.setHandlers(new Handler[]{handler});
        // SSLの使用判定
        Boolean ssIFlag = Boolean.parseBoolean(properties.getProperty("sslUseFlag"));

        logger.debug("sslFlag : " + ssIFlag);
        if (ssIFlag) {
            final SslContextFactory sslContextFactory = new SslContextFactory.Server();
            // キーストアファイル名を設定
            sslContextFactory.setKeyStorePath(properties.getProperty("sslCertFilePatah"));
            // password
            sslContextFactory.setKeyStorePassword(properties.getProperty("sslCertPassword"));
            // サーバコネクタ作成
            final ServerConnector httpsConnector = new ServerConnector(server, sslContextFactory);
            // HTTPS通信のためのPORT を設定
            httpsConnector.setPort(Integer.parseInt(properties.getProperty("sslCertPort ")));
            server.setConnectors(new Connector[]{httpsConnector});
        }

        // instance終了でサーバを停止
        server.setStopAtShutdown(true);
        // ハンドラーを指定
        server.setHandler(handlerList);
        try { // Jettyを開始
            server.start();
            logger.info("Jetty起動完了");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * doPost このメソッドは以下の機能を提供します。 証結果通知確認 標準エッジから受け付けたHTTPリクエストからボディの内容をログ出力
     * 証結果通知応答認証結果通知で受け付けた statusCodeに応じてレスポンスを返却します
     *
     * @param HttpServlet Request req エッジリクエスト
     * @param HttpServlet Response resp エッジレスポンス
     * @throws ServletException, IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // 設定ファイル読み込み
          /*  final ResourceBundle rb = ResourceBundle.getBundle("ExtIFStub", Locale.getDefault(),
                    new URLClassLoader(new URL[]{Paths.get("./").toFile().toURI().toURL()}));*/

            // リクエスト､レスポンスの設定を指定
            req.setCharacterEncoding("UTF-8");
            resp.setContentType("text/json; charset=UTF-8");

            logger.info(ThreadContext.get("tid") + " : " + ThreadContext.get("pid"));

            // 起動時のスレッド単位のIDでリプレイス
            ThreadContext.put("tid", TID);
            // ランダムで生成したタスク単位の数値でリプレイス
            ThreadContext.put("pid",
                    String.valueOf(new Random().nextInt(Integer.parseInt(properties.getProperty("taskIdMaxValue")))));

            // インスタンス生成
            ObjectMapper mapper = new ObjectMapper();
            CheckParameter cp = new CheckParameter();
            AnalysisJson aj = new AnalysisJson();

            // 定義
            String responseJson = "";
            int targetStatusCode = 0;
            ResponseDto response = new ResponseDto();

            // HTTPリクエストからボディを取得し request Bodyに格納
            String requestBody = req.getReader().lines().collect(Collectors.joining("\r\n"));
            logger.debug("RequestBody:" + requestBody);
            // ダブルクォートなしでJSONをパースできるように設定をONにする
            mapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            // Json Java オブジェクトに変換
            RequestDto requestDto = mapper.readValue(requestBody, RequestDto.class);

            StringBuilder sb = new StringBuilder();
            // リクエストパラメータ必須チェック
            if ((targetStatusCode = cp.checkParameter(requestDto)) != 0) {
                // 必須チェック判定がOKの場合、リクエスト情報のログ出力

                sb.append("\r\n<>req start<>\r\n認証結果受信時刻："
                        + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").format(Calendar.getInstance().getTime())
                        + "\r\n認証ステータス：" + requestDto.getStatus() + "\r\n認証ステータスコード：" + requestDto.getStatusCode()
                        + "\r\n認証ステータスメッセージ：" + requestDto.getStatusMessage() + "\r\n認証ID：" + requestDto.getAuthId() + "\r\n対象者物理ID："
                        + requestDto.getUserOId() + "\r\n対象利用者ID：" + requestDto.getUserId() + "\r\n対象端末ID：" + requestDto.getEdgeId()
                        + "\r\n<>req end<>");

                logger.info(sb.toString());
            } else {
                logger.info("無効なstatusCodeです");
                targetStatusCode = 400;
            }

            response = aj.analysisJson(targetStatusCode, properties);
            // 処理遅延
            TimeUnit.MILLISECONDS.sleep(Integer.parseInt(properties.getProperty("processingDelayTime")));
            //HTTPレスポンスの設定
            resp.setStatus(response.getReturnCode());
            // response return
            PrintWriter out = resp.getWriter();
            out.print(responseJson);
            out.close();

        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}