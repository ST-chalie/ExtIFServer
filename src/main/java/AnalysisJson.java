import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AnalysisJson {

    public String analysisJson(int targetStatusCode, ResourceBundle rb)
            throws StreamReadException, DatabindException, IOException {

        ObjectMapper mapper = new ObjectMapper();
        ResponseDto response = new ResponseDto();

        int resCode = 0;
        String jsonFileName = null;
        MappingJsonDto[] mjd = mapper.readValue(
                new File(rb.getString("mappingFilePath")),
                MappingJsonDto[].class);

        for (MappingJsonDto dto : mjd) {
            if (dto.statusCode == targetStatusCode) {
                resCode = dto.returnCode;
                jsonFileName = dto.messageName;
            }
        }

        MessageDto md = mapper.readValue(new File
                        (rb.getString("messageNameFilePath") + jsonFileName),
                MessageDto.class);

        response.returnCode = resCode;
        response.resMessage = md.text;

        return mapper.writeValueAsString(response);
    }
}
