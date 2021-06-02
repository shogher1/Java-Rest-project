import com.devskiller.jfairy.Fairy;
import java.util.Random;


public class Functions {
    static String AccessToken = "Bearer ad1df24eb1758ed69bb7664960b7641b7201fb7598e38e0e22cfe8d78ebb921c";
        protected static String Email() {
            String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
            StringBuilder salt = new StringBuilder();
            Random rnd = new Random();
            while (salt.length() < 10) {
                int index = (int) (rnd.nextFloat() * SALTCHARS.length());
                salt.append(SALTCHARS.charAt(index));
            }
            String saltStr = salt.toString();
            return saltStr;
        }
        public static String gender() {
            Fairy fairy = Fairy.create();
            String str = fairy.person().getSex().toString().toLowerCase();
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }
}
