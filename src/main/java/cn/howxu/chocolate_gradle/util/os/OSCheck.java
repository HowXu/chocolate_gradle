package cn.howxu.chocolate_gradle.util.os;

public class OSCheck {
    public static String OS_Name = System.getProperty("os.name");
    public static String OS_Arch = System.getProperty("os.arch");

    public static OS checkOS() {

        OS _os = new OS();

        if (OS_Arch.contains("64")){
            _os.setArch("64");
        }else {
            _os.setArch("32");
        }

        //应该用个字符串检测库的我了个豆，这个效率太低
        if (OS_Name.contains("Windows") || OS_Name.contains("win")){
            _os.setOs("windows");
        }

        if (OS_Name.contains("Mac") || OS_Name.contains("mac") || OS_Name.contains("osx")){
            _os.setOs("osx");
        }

        if (OS_Name.contains("Linux") || OS_Name.contains("linux") || OS_Name.contains("unix")){
            _os.setOs("linux");
        }

        return _os;
    }
}
