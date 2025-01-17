import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class AnalysisJson {

    public ResponseDto analysisJson(int targetStatusCode, Properties properties)
            throws StreamReadException, DatabindException, IOException {

        ObjectMapper mapper = new ObjectMapper();
        ResponseDto response = new ResponseDto();
        int text;
        StringBuilder sb = new StringBuilder();
        int resCode = 0;
        String jsonFileName = null;

        MappingJsonDto[] mjd = mapper.readValue(
                new File(properties.getProperty("mappingFilePath")),
                MappingJsonDto[].class);

        for (MappingJsonDto dto : mjd) {
            if (dto.getStatusCode() == targetStatusCode) {
                resCode = dto.getReturnCode();
                jsonFileName = dto.getMessageName();
            }
        }

        FileReader fr = new FileReader(
                new File(properties.getProperty("messageNameFilePath")
                        + jsonFileName), StandardCharsets.UTF_8);

        // ファイル読み込み
        while ((text = fr.read()) != -1) {
            sb.append((char) text);
        }
        fr.close();
/*        MessageDto md = mapper.readValue(new File
                        (properties.getProperty("messageNameFilePath") + jsonFileName),
                MessageDto.class);*/

        response.setReturnCode(resCode);
        response.setResMessage(sb.toString());

        return response;
    }
}
