package structure;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DirectoryParser {

    public static void main(String[] args) {
        String filePath = "C:\\Users\\29202\\Desktop\\other\\咸鱼项目\\记事本项目\\完美世界.txt";
        String directorrFilePath = "C:\\Users\\29202\\Desktop\\other\\咸鱼项目\\记事本项目\\directory.json";

        JSONArray directories = readTextFile(filePath);
        saveDirectoriesToJsonFile(directories, directorrFilePath);


        // List<JSONObject> directories = extractDirectories(text);
        // 将目录列表保存为 JSON 文件
        // saveDirectoriesToJsonFile(directories, "./directoriesStructure_test.json");
    }

    /**
     * 读取文本文件，解析出目录结构
     *
     * @param filePath 文本路径
     * @return JSONArray目录结构
     */
    public static JSONArray readTextFile(String filePath) {
        // 暂时存储目录接哦古
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
                // 找到的是1级标题
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
                    // 找到的是2级标题
                    if (level2Matcher.find()) {
                        // System.out.println(line);
                        String chapterTitle = level2Matcher.group(0);
                        // System.out.println("2级标题 " + chapterTitle);
                        level2TitleObject = new JSONObject();
                        level3TitlesArray = new JSONArray();
                        // level2TitleObject.put("title", chapterTitle);
                        level2TitleObject.put("title", line);
                        level2TitleObject.put("lineIndex", pageIndex);
                        level2TitleObject.put("subTitles", level3TitlesArray);
                        level2TitlesArray.add(level2TitleObject);


                    } else {
                        level3Matcher = level3Pattern.matcher(line);
                        // 找到的是3级标题
                        if (level3Matcher.find()) {
                            // System.out.println(line);
                            String chapterTitle = level3Matcher.group(0);
                            // System.out.println("3级标题 " + chapterTitle);
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
     * 解析出的目录村存储至本地
     *
     * @param directories 目录结构对象
     * @param filePath    目录文件位置
     */
    public static void saveDirectoriesToJsonFile(JSONArray directories, String filePath) {
        // 若文件不存在，则需要创建文件
        File file = new File(filePath);
        if (file.exists())
            file.delete();
        file.delete(); // 删除文件，防止更新目录有之前的目录残留
        try {
            if (file.createNewFile()) {
                // System.out.println("文件创建成功");
            } else {
                // System.out.println("文件创建失败");
            }
        } catch (IOException e) {
            // System.out.println("创建文件时发生异常: " + e.getMessage());
        }

        try (FileOutputStream fos = new FileOutputStream(filePath);
             OutputStreamWriter writer = new OutputStreamWriter(fos, "GBK")) {
            writer.write(directories.toJSONString());

            // System.out.println("目录已保存到文件: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            // System.out.println("保存文件时出错: " + e.getMessage());
        }


        // try (FileWriter fileWriter = new FileWriter(filePath)) {
        //     fileWriter.write(directories.toJSONString());
        // } catch (IOException e) {
        // }
    }
}
