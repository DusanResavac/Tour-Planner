package TourProject.Model.MainWindow;

import lombok.Getter;

public class AttributeMapper {
    @Getter
    private final String getAttribute;
    @Getter
    private final String setAttribute;
    @Getter
    private final Class<?> attributeClass;

    public AttributeMapper(String getAttribute, String setAttribute, Class<?> attributeClass) {
        this.getAttribute = getAttribute;
        this.setAttribute = setAttribute;
        this.attributeClass = attributeClass;
    }
}
