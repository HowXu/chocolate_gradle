package cn.howxu.chocolate_gradle.util.os;

public class OS {
    //防止某些脑瘫设备刷不出来
    private String arch = "";
    private String os = "";

    public String getArch() {
        return arch;
    }

    public String getOs() {
        return os;
    }

    public void setArch(String arch) {
        this.arch = arch;
    }

    public void setOs(String os) {
        this.os = os;
    }
}
