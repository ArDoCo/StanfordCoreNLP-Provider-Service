package stanfordnlp;

import edu.kit.kastel.mcse.ardoco.core.api.text.Text;
import io.github.ardoco.textproviderjson.converter.ObjectToDtoConverter;
import io.github.ardoco.textproviderjson.dto.TextDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@RestController
public class TextController {

    @GetMapping ("/stanfordnlp")
    public TextDTO texting(@RequestParam(defaultValue = "The quick brown fox jumped over the lazy dog.") String text) {
        InputStream inputStream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        Text annotatedText = processText(inputStream);
        return convertToDto(annotatedText);
    }

    private Text processText(InputStream text) {
        CoreNLPProvider nlpProvider = new CoreNLPProvider();
        return nlpProvider.processText(text);
    }

    private TextDTO convertToDto(Text text) {
        ObjectToDtoConverter converter = new ObjectToDtoConverter();
        return converter.convertTextToDTO(text);
    }

}
