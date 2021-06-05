package TourProject.Model.api;

import lombok.Getter;

import java.util.List;

public class OpenAIResponseBody {
    @Getter
    public List<OpenAIChoice> choices;
}
