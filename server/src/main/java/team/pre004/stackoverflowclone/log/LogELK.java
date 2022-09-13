package team.pre004.stackoverflowclone.log;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LogELK {
    String timestamp;
    String hostname;
    String hostIp;
    String clientIp;
    String clientUrl;
    String callFunction;
    String type;
    String parameter;
}
