package landr.parser;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import landr.parser.syntax.Help;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * This utility class can create {@link Help} from JSON files.
 */
public class JacksonHelpParser {

    private final ObjectMapper jackson;

    public JacksonHelpParser() {

        JsonFactory factory = new JsonFactory();
        jackson = new ObjectMapper(factory);
    }

    /**
     * Constructor to be used only when part of another parser.
     */
    JacksonHelpParser(ObjectMapper jackson) {
        this.jackson = jackson;
    }

    public Help readHelp(Reader reader) throws IOException {

        return jackson.readValue(reader, ParsedHelp.class);

    }

    /**
     * Simple implementation deserialized from the JSON files.
     */
    static class ParsedHelp implements Help {

        private final String section;
        private final String summary;
        private final String details;
        private final List<String> usages;

        @JsonCreator
        public ParsedHelp(
                @JsonProperty(value = "section") String section,
                @JsonProperty(value = "summary") String summary,
                @JsonProperty(value = "details") String details,
                @JsonProperty(value = "usages") List<String> usages) {
            this.section = section;
            this.summary = summary;
            this.details = details;
            this.usages = usages;
        }

        @Override
        public String getSection() {
            return section;
        }

        @Override
        public String getSummary() {
            return summary;
        }

        @Override
        public String getDetails() {
            return details;
        }

        @Override
        public List<String> getUsages() {
            return usages;
        }
    }
}
