package client;


public class Correct {


    static boolean pswCheck (String psw){
        //проверяет что должень быть минимум 1 символ 1 цифра 1 строчная и 1 заглавная буквы, длина минимум 8 символов
        String pPattern =  "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[-!$%^&*()_+|~=`{}\\[\\]:\";'<>?,./#@])(?=\\S+$).{8,}";

        if (psw.length() == 0 || psw == null || psw.length() < 8){
            return false;

        }else  {

            return psw.matches(pPattern);
        }

    }

    //Check if e-mail adress is valid based on
    //https://stackoverflow.com/questions/624581/what-is-the-best-java-email-address-validation-method
    //and
    //https://www.geeksforgeeks.org/check-email-address-valid-not-java/
    static boolean isValidEmail(String email){
        //The regular expression is used from the second link
        String ePattern = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                          "[a-zA-Z0-9_+&*-]+)*@" +
                          "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                          "A-Z]{2,7}$";

        return email.matches(ePattern);
    }
}
