import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectIndexRequest {

    private final Long id;
    private final String name;
    private final String projectExplain;
    private final List<String> resultImages;
    private final List<String> teamStacks;
    private final String title;
}
