0.buildして、jarを最新にする
1.batchでjarを起動
C:\Users\satoshi\IdeaProjects\ExtIFServer\startServer.bat

2.Jetty（Servlet）起動が完了したら、コマンドプロンプトから以下のcurlをたたく
G:\intelij\ExtIFServer
・成功
curl -X POST -H "Content-Type: application/json" http://localhost:8080 -d @G:\intelij\ExtIFServer/src/test/resources/test1.json
・失敗
curl -X POST -H "Content-Type: application/json" http://localhost:8080 -d @G:\intelij\ExtIFServer/src/test/resources/test2.json
・エラー
curl -X POST -H "Content-Type: application/json" http://localhost:8080 -d @G:\intelij\ExtIFServer/src/test/resources/test3.json

3.
responseがjsonで帰ってきたら成功
ーーーーーーーーーーーーーーーーーーーーーーー

curl http://localhost:8080 -d
--cd /D D:\develop\pleiades\workspace\ExtIFStub
cd C:/Users/satoshi/IdeaProjects/ExtIFServer

