import com.google.gson.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
//这是一个工具类
class requests {
    public String use(CloseableHttpClient client, String stuNum) throws Exception{
        String url = "http://jwzx.cqupt.edu.cn/data/json_StudentSearch.php?searchKey=";
        CloseableHttpResponse response = null;
        String co = null;
        try {
            HttpGet httpGet = new HttpGet(url + stuNum);
            //httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36");
            response = client.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                co = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } finally {
            if (response != null) response.close();
        }
        return co;
    }

    public String numMaker(int year, int num){
        String re ;
        if (num < 10) {
            re = "0" + num;
        } else {
            re = Integer.toString(num);
        }
        return "201" + year + "21" + re;
    }
}

class Th2 implements Runnable {
    private int year;
    private ArrayList<String> contents;

    public ArrayList<String> getContents() {
        return contents;
    }

    Th2 (int a) {
        this.year = a + 1;
        contents = new ArrayList<String>();
    }

    @Override
    public void run() {
        requests a = new requests();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            for (int i = 0; i < 100; i++){
                contents.add(a.use(httpClient ,a.numMaker(this.year, i)));
            }
            httpClient.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

class jwzxFucker {
    private ArrayList<Th2> re;
    private ArrayList<StudentPro> studentPros;

    public ArrayList<StudentPro> getStudentPros() {
        return studentPros;
    }

    public ArrayList<Th2> getRe() {
        return re;
    }

    jwzxFucker(){
        re = new ArrayList<Th2>();
        studentPros = new ArrayList<StudentPro>();
    }
    public void fuck() {
        Thread[] threads = new Thread[8];//我电脑是8线程 就开了8个
        for (int i = 0; i < 8; i++) {
            re.add(new Th2(i));
            threads[i] = new Thread(re.get(i));
            threads[i].start();
        }
        for (int i = 0; i < 8; i++){
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void parsOne(String re){
        JsonObject jsonObject = new JsonParser().parse(re).getAsJsonObject();
        JsonArray jsonElements = jsonObject.getAsJsonArray("returnData");
        Gson gson = new Gson();
        if (jsonElements.size() != 0) {
            for (JsonElement stu : jsonElements) {
                StudentPro studentPro = gson.fromJson(stu, StudentPro.class);
                this.studentPros.add(studentPro);
            }
        }
    }

    public void pars(){
        for (int i = 0; i < re.size(); i++) {
            for (int n = 0; n < re.get(i).getContents().size(); n++) {
                parsOne(re.get(i).getContents().get(n));
            }
        }
    }
}

public class test {
    public static void main(String[] args) {
        ArrayList<Student> students = new ArrayList<Student>();//用于存储学生ID
        jwzxFucker fucker = new jwzxFucker();//new一个Fucker
        System.out.println("start");
        fucker.fuck();//开始Fuck
        System.out.println("end");
        fucker.pars(); //解析JSON
        //获取的都是真实学号
        for (StudentPro studentPro : fucker.getStudentPros()){
            Student student = new Student();
            student.setIfReal(true);
            student.setStuId(studentPro.getXh());
            students.add(student);
        }

        System.out.println(students.size());
    }
}
