package tree_parity_machine;

public class InputUnmatchException extends RuntimeException {
    public InputUnmatchException(){
        super("输入规模不匹配！");
    }
}
