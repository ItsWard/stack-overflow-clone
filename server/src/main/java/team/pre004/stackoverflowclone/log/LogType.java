package team.pre004.stackoverflowclone.log;

import lombok.Getter;
//import lombok.RequiredAr
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LogType {
    CONTROLLER("CONTROLLER"),
    SERVICE("SERVICE"),
    DATABASE("DATABASE");

    private final String type;

}
