package client.utils;


public class Correct {
    public static boolean isValidEmail(String email){
        //The regular expression is used from the second link
        String ePattern = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches() && email.length() <= 64;
    }

    public static int checkPasswordStrength(String password) {
        int strengthPercentage=0;
        String[] partialRegexChecks = { ".*[a-z]+.*", // lower
                ".*[A-Z]+.*", // upper
                ".*[\\d]+.*", // digits
                ".*[@#$%!]+.*" // symbols
        };

        if (password.matches(partialRegexChecks[0])) {
            strengthPercentage+=1;
        }
        if (password.matches(partialRegexChecks[1])) {
            strengthPercentage+=1;
        }
        if (password.matches(partialRegexChecks[2])) {
            strengthPercentage+=1;
        }
        if (password.matches(partialRegexChecks[3])) {
            strengthPercentage+=1;
        }
        if (password.length()>=8){
            strengthPercentage+=1;
        }

        return strengthPercentage;
    }
}
