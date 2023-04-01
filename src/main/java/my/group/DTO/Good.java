package my.group.DTO;

public class Good {
    private String good_name;
    private int typeId;
    private int brandId;

    public Good(String good_name, int typeId, int brandId) {
        this.good_name = good_name;
        this.typeId = typeId;
        this.brandId = brandId;
    }

    public String getGood_name() {
        return good_name;
    }

    public void setGood_name(String good_name) {
        this.good_name = good_name;
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
                "good_name='" + good_name + '\'' +
                ", typeId=" + typeId +
                ", brandId=" + brandId +
                '}';
    }
}
