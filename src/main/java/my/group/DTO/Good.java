package my.group.DTO;

import javax.validation.constraints.NotBlank;

public class Good {
    private String goodName;
    private int typeId;
    private int brandId;

    public Good(String good_name, int typeId, int brandId) {
        this.goodName = good_name;
        this.typeId = typeId;
        this.brandId = brandId;
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

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    @Override
    public String toString() {
        return "Good{" +
                "good_name='" + goodName + '\'' +
                ", typeId=" + typeId +
                ", brandId=" + brandId +
                '}';
    }
}
