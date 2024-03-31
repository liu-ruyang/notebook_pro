package structure;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DirectoryParser {

    public static void main(String[] args) {
        String filePath = "C:\\Users\\29202\\Desktop\\other\\������Ŀ\\���±���Ŀ\\��������.txt";
        String directorrFilePath = "C:\\Users\\29202\\Desktop\\other\\������Ŀ\\���±���Ŀ\\directory.json";

        JSONArray directories = readTextFile(filePath);
        saveDirectoriesToJsonFile(directories, directorrFilePath);


        // List<JSONObject> directories = extractDirectories(text);
        // ��Ŀ¼�б���Ϊ JSON �ļ�
        // saveDirectoriesToJsonFile(directories, "./directoriesStructure_test.json");
    }

    /**
     * ��ȡ�ı��ļ���������Ŀ¼�ṹ
     *
     * @param filePath �ı�·��
     * @return JSONArrayĿ¼�ṹ
     */
    public static JSONArray readTextFile(String filePath) {
        // ��ʱ�洢Ŀ¼��Ŷ��
        JSONArray level1TitlesArray = new JSONArray();
        JSONArray level2TitlesArray = new JSONArray();
        JSONArray level3TitlesArray = new JSONArray();
        JSONObject level1TitleObject;
        JSONObject level2TitleObject;
        JSONObject level3TitleObject;

        int pageIndex = 1;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String titleLevel1 = "^\\s*\\d+(\\s|$)";
            String titleLevel2 = "^\\s*\\d+\\.\\d+(\\s|$)";
            String titleLevel3 = "^\\s*\\d+\\.\\d+\\.\\d+(\\s|$)";
            String line;
            while ((line = reader.readLine()) != null) {
                Pattern level1Pattern = Pattern.compile(titleLevel1);
                Pattern level2Pattern = Pattern.compile(titleLevel2);
                Pattern level3Pattern = Pattern.compile(titleLevel3);
                Matcher level1Matcher;
                Matcher level2Matcher;
                Matcher level3Matcher;


                level1Matcher = level1Pattern.matcher(line);
                // �ҵ�����1������
                if (level1Matcher.find()) {
                    // System.out.println(line);
                    String chapterTitle = level1Matcher.group(0);
                    level1TitleObject = new JSONObject();
                    level2TitlesArray = new JSONArray();
                    // level1TitleObject.put("title", chapterTitle);
                    level1TitleObject.put("title", line);
                    level1TitleObject.put("lineIndex", pageIndex);
                    level1TitleObject.put("subTitles", level2TitlesArray);
                    level1TitlesArray.add(level1TitleObject);

                } else {
                    level2Matcher = level2Pattern.matcher(line);
                    // �ҵ�����2������
                    if (level2Matcher.find()) {
                        // System.out.println(line);
                        String chapterTitle = level2Matcher.group(0);
                        // System.out.println("2������ " + chapterTitle);
                        level2TitleObject = new JSONObject();
                        level3TitlesArray = new JSONArray();
                        // level2TitleObject.put("title", chapterTitle);
                        level2TitleObject.put("title", line);
                        level2TitleObject.put("lineIndex", pageIndex);
                        level2TitleObject.put("subTitles", level3TitlesArray);
                        level2TitlesArray.add(level2TitleObject);


                    } else {
                        level3Matcher = level3Pattern.matcher(line);
                        // �ҵ�����3������
                        if (level3Matcher.find()) {
                            // System.out.println(line);
                            String chapterTitle = level3Matcher.group(0);
                            // System.out.println("3������ " + chapterTitle);
                            level3TitleObject = new JSONObject();
                            // level3TitleObject.put("title", chapterTitle);
                            level3TitleObject.put("title", line);
                            level3TitleObject.put("lineIndex", pageIndex);
                            level3TitlesArray.add(level3TitleObject);
                        }
                    }
                }
                pageIndex++;
                // level1Pattern.matcher(line);
                // Matcher matcher = pattern.matcher(text);
            }
            // System.out.println(level1TitlesArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return level1TitlesArray;
    }

    /**
     * ��������Ŀ¼��洢������
     *
     * @param directories Ŀ¼�ṹ����
     * @param filePath    Ŀ¼�ļ�λ��
     */
    public static void saveDirectoriesToJsonFile(JSONArray directories, String filePath) {
        // ���ļ������ڣ�����Ҫ�����ļ�
        File file = new File(filePath);
        if (file.exists())
            file.delete();
        file.delete(); // ɾ���ļ�����ֹ����Ŀ¼��֮ǰ��Ŀ¼����
        try {
            if (file.createNewFile()) {
                // System.out.println("�ļ������ɹ�");
            } else {
                // System.out.println("�ļ�����ʧ��");
            }
        } catch (IOException e) {
            // System.out.println("�����ļ�ʱ�����쳣: " + e.getMessage());
        }

        try (FileOutputStream fos = new FileOutputStream(filePath);
             OutputStreamWriter writer = new OutputStreamWriter(fos, "GBK")) {
            writer.write(directories.toJSONString());

            // System.out.println("Ŀ¼�ѱ��浽�ļ�: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            // System.out.println("�����ļ�ʱ����: " + e.getMessage());
        }


        // try (FileWriter fileWriter = new FileWriter(filePath)) {
        //     fileWriter.write(directories.toJSONString());
        // } catch (IOException e) {
        // }
    }
}
