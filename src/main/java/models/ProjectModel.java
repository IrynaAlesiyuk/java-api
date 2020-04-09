package models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectModel {

    private String projectId;
    private String projectName;
    private String projectDescription;
    private String projectStartsAt;
    private String projectEndsAt;

}
