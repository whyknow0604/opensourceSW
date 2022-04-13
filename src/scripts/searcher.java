package scripts;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;


public class searcher {
    private String data_path;
    private String query = "";

    public searcher(String path, String query) {
        this.data_path = path;
        this.query = query;
    }

    public void CalcSim() throws Exception {

        FileInputStream fileStream = new FileInputStream(data_path);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileStream);

        Object object = objectInputStream.readObject();
        objectInputStream.close();
        HashMap hashmap = (HashMap)object;

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse("collection.xml");
        Element root = doc.getDocumentElement();
        NodeList list = root.getElementsByTagName("title");

        KeywordExtractor ke = new KeywordExtractor();
        KeywordList klBody = ke.extractKeyword(query,true);

        double[] result = new double[list.getLength()];

        for(int result_index = 0; result_index < list.getLength(); result_index ++){
            String wq[];
            for (int j = 0; j< klBody.size(); j++) {
                Keyword kwrd = klBody.get(j);

                if(hashmap.get(kwrd.getString()) == null){
                    System.out.println(kwrd.getString() + "은 검색된 문서가 없습니다.");
                }else {
                    wq = String.valueOf(hashmap.get(kwrd.getString())).split(" ");
                    result[result_index] += Double.parseDouble(wq[2+(result_index*2)]) * kwrd.getCnt();
                }
            }
        }

        int[] bigindex = new int[3];
        double max;
        int m_index = -1;
        int i;
        for(i = 0; i < 3; i++){
            max = 0;
            for(int j = 0; j < result.length; j++){
                if(max < result[j]){
                    max = result[j];
                    m_index = j;
                }
            }
            if(m_index != -1){
                result[m_index] = -1 * result[m_index];
                bigindex[i] = m_index;
            }else{
                break;
            }

        }

        for(int j = 0; j < i; j++){
            System.out.println( Math.round((result[bigindex[j]]*-1)*100)/100.0 + " : "  + list.item(bigindex[j]).getTextContent());
        }

    }

}