package TourProject.Model.api;

import lombok.Getter;
import lombok.Setter;

public class OpenAIRequest {
    @Getter @Setter
    public String prompt;
    @Getter @Setter
    public Double temperature;
    @Getter @Setter
    public Integer max_tokens;
    @Getter @Setter
    public Double top_p;
    @Getter @Setter
    public Double frequency_penalty;
    @Getter @Setter
    public Double presence_penalty;
}
