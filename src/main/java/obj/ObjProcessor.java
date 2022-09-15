package obj;


public class ObjProcessor {
    public String toString(Object input) {
        return input.toString();
    }

    public int toInt(Object input) {
        String obj = input.toString();
        int res = Integer.parseInt(obj);
        return res;
    }
}
