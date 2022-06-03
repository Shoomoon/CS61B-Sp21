package capers;

import java.io.*;
import static capers.Utils.readObject;
import static capers.Utils.writeObject;

public class filetest {
    public static void main(String[] arg) throws IOException {
//        Model m_out = new Model();
//        String saveFileName = "FileRecord@20220531";
//        File outFile = new File(saveFileName);
//        try {
//            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outFile));
//            out.writeObject(m_out);
//            out.close();
//        } catch (IOException excp) {
//            System.out.println("Save data failure");
//        }
//        Model m_in;
//        File inFile = new File(saveFileName);
//        try {
//            ObjectInputStream inp = new ObjectInputStream(new FileInputStream(inFile));
//            m_in = (Model) inp.readObject();
//            inp.close();
//        } catch (IOException | ClassNotFoundException excp) {
//            System.out.println("Read saved data failure");
//            m_in = null;
//        }


        File file = new File("dummy" + ".txt");
        String story = "Hello I am Penny! I am the cutest dog in this unit!";
        Utils.writeObject(file, story);
        String result = Utils.readObject(file, String.class);
        System.out.println(result);
//        Model m = new Model();
//        File outFile = new File("dummy.txt");
//        writeObject(outFile, m);
    }
}
