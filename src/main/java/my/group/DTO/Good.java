package my.group.DTO;

import javax.validation.constraints.NotBlank;

public class Good {
    private String goodName;
    private int typeId;

    public Good(String goodName, int typeId) {
        this.goodName = goodName;
        this.typeId = typeId;

    }
    @NotBlank (message = "Yor name good is blank")
    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    @Override
    public String toString() {
        return "Good{" +
                "good_name='" + goodName + '\'' +
                ", typeId=" + typeId +
                '}';
    }
}
