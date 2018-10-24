package client.utils;


public class Correct {

    //Password check based on https://www.codemiles.com/java-examples/simple-password-strength-checker-t7249.html
    static int pswCheck (String psw){
        int index = 0;
        //Regular expressions checked with https://regex101.com/
        String[] partialRegexChecks = { "^[a-z]+$", // lower
                "^[A-Z]+$", // upper
                "^[A-Za-z]+$", // upper and lower
                "^[\\d]+$", // digits
                "^[-!$%^&*()_+|~=`{}\\[\\]:\";'<>?,./#@]*$" // symbols, did not check if all symbols are counted
        };
        if (psw.length() == 0 || psw == null) return 0;
        else if (psw.length() < 8) index += 2;
        else if (psw.length() < 12) index += 3;
        else index += 4;
        if (psw.matches(partialRegexChecks[0]) || psw.matches(partialRegexChecks[1]))
            index = Math.round((float)index/2);
        else if (psw.matches(partialRegexChecks[2]) || psw.matches(partialRegexChecks[3]) ||
                psw.matches(partialRegexChecks[4]))
            index = Math.round((float)(index+1)/2);
        else if (psw.matches(partialRegexChecks[2]) && psw.matches(partialRegexChecks[3]) ||
                psw.matches(partialRegexChecks[2]) && psw.matches(partialRegexChecks[4]) ||
                psw.matches(partialRegexChecks[3]) && psw.matches(partialRegexChecks[4]))
            index = Math.round((float)(index+3)/2);
        else index = Math.round((float)(index+4)/2);
        // Description: 0 - restricted password; 1 - very weak; 2 - weak; 3 - normal; 4 - strong
        return index;

    }

    //Check if e-mail adress is valid based on
    //https://stackoverflow.com/questions/624581/what-is-the-best-java-email-address-validation-method
    //and
    //https://www.geeksforgeeks.org/check-email-address-valid-not-java/
    public static boolean isValidEmail(String email){
        //The regular expression is used from the second link
        String ePattern = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }
}
