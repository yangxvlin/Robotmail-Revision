package exceptions;

/**
 * Xulin Yang, 904904
 *
 * @create 2019-04-25 22:52
 * description:
 **/

public class UnSupportedTooMuchRobotException extends Throwable    {
    public UnSupportedTooMuchRobotException() {
        super("More than 3 robots inputted!");
    }
}