
package asymmetric;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;


public class MainUIController implements Initializable {

    @FXML
    private TextField text_original;
    @FXML
    private TextArea text_encrypted;
    @FXML
    private TextArea text_decrypted;
    @FXML
    private TextField text_public;
    @FXML
    private TextField text_private;
    @FXML
    private Button button_run;
    @FXML
    private Button button_bpr;
    @FXML
    private Button button_bpk;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        button_run.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                String original = text_original.getText();
                String privatepath = text_private.getText();
                String publicpath = text_public.getText();

                try
                {
                    Cipher cipher = Cipher.getInstance("RSA");

                    //fetching private key
                    byte[] keyBytes = Files.readAllBytes(new File(privatepath).toPath());
                    PKCS8EncodedKeySpec pspec = new PKCS8EncodedKeySpec(keyBytes);
                    KeyFactory kf = KeyFactory.getInstance("RSA");
                    PrivateKey private_key = kf.generatePrivate(pspec);

                    //fetching pubilc key
                    keyBytes = Files.readAllBytes(new File(publicpath).toPath());
                    X509EncodedKeySpec xspec = new X509EncodedKeySpec(keyBytes);
                    kf = KeyFactory.getInstance("RSA");
                    PublicKey public_key = kf.generatePublic(xspec);

                    //encrypting text using private key
                    cipher.init(Cipher.ENCRYPT_MODE, private_key);
                    String encrypted = Base64.encodeBase64String(cipher.doFinal(original.getBytes("UTF-8")));
                    text_encrypted.setText(encrypted);

                    //decrypting previously encrypted text using public key
                    cipher.init(Cipher.DECRYPT_MODE, public_key);
                    String decrypted = new String(cipher.doFinal(Base64.decodeBase64(encrypted)), "UTF-8");
                    text_decrypted.setText(decrypted);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        button_bpk.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                FileChooser fc = new FileChooser();
                File file = fc.showOpenDialog(new Stage());
                text_public.setText(file.toPath().toString());
            }
        });

        button_bpr.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                FileChooser fc = new FileChooser();
                File file = fc.showOpenDialog(new Stage());
                text_private.setText(file.toPath().toString());
            }
        });
    }
    
}
