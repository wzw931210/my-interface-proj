package cn.jinjing.plat.digital.pojo;

public class LabelInfo {
    private String label;
    private String indexStart;
    private String indexEnd;
    private String type;

    public LabelInfo(String label,String indexStart,String indexEnd,String type){
        this.label = label;
        this.indexStart = indexStart;
        this.indexEnd = indexEnd;
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    public String getIndexStart() {
        return indexStart;
    }

    public void setIndexStart(String indexStart) {
        this.indexStart = indexStart;
    }

    public String getIndexEnd() {
        return indexEnd;
    }

    public void setIndexEnd(String indexEnd) {
        this.indexEnd = indexEnd;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
