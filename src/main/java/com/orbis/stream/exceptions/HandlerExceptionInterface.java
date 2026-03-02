package com.orbis.stream.exceptions;

import com.orbis.exception.annotations.ExceptionMaker;
import com.orbis.exception.annotations.ExceptionRunner;

@ExceptionMaker(classesName = {"NotValidException"})
public interface HandlerExceptionInterface {

    @ExceptionRunner(exceptionClass = "NotValidException", componentModel = "spring")
    void runnerNotValidException(String message);

}
