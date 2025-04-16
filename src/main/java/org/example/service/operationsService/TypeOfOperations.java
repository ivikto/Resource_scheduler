package org.example.service.operationsService;

public enum TypeOfOperations {

    // Лазерный резчик
    LASER_WORKER_HOURS("СПЦФ трудочаса рабочего лазер"),
    LASER_CUTTING("СПЦФ ПР рез деталей на лазерном резчике"),
    LASER_FINISHING_WORK("СПЦФ трудочаса финишные работы"),
    LASER_FINISHING_WORK2("СПЦФ трудочаса финишные работы "),

    //Лазерная чистка
    LASER_CLEANING_PR("СПЦФ ПР лазерная чистка"),
    LASER_CLEANING("СПЦФ трудочаса рабочего лазер_чистка"),

    // Токарные операции
    LATHE_WORKER_HOURS("СПЦФ трудочаса рабочего токарный"),
    LATHE_PR("СПЦФ ПР токарные работы"),

    // Фрезерные операции
    MILLING_WORKER_HOURS("СПЦФ трудочаса рабочего фрезер"),
    MILLING_PR("СПЦФ ПР фрезерные работы"),

    // Сварочные операции
    WELDING_STEEL("СПЦФ ПР сварочные работы: Сталь"),
    WELDING_MULTI("СПЦФ ПР сварочные работы: Сталь, Нержавейка, Алюминий, Медь"),
    WELDING_WORKER_HOURS("СПЦФ трудочаса рабочего сварка"),
    WELDING_LASER("СПЦФ трудочаса лазер_сварка"),

    // 3D печать
    PRINT_3D_PR("СПЦФ ПР печать на 3D принтере"),
    PRINT_3D_WORKER_HOURS("СПЦФ трудочаса рабочего 3D печать"),

    // Монтажные работы
    MONTAGE_ASSEMBLY_WORKER_HOURS("СПЦФ трудочаса рабочего монтаж"),
    MONTAGE_COMPLEX_ASSEMBLY("СПЦФ трудочаса монтаж сложный"),
    MONTAGE_ASSEMBLY_PR("СПЦФ ПР монтажные работы"),

    //Малярные работы
    PAINTING_WORKER_HOURS("СПЦФ трудочаса рабочего малярка"),
    PAINTING_POWDER_COATING("СПЦФ ПР нанесение порошковой краски"),
    PAINTING_POWDER_PRIMER("СПЦФ ПР нанесение порошкового грунта"),

    //Трубогиб
    PIPE_BENDING("СПЦФ трудочаса трубогиб"),
    PIPE_CNC_BENDING("СПЦФ ПР гиб на ЧПУ на трубогибе"),

    //Вальцевание
    ROLLING("СПЦФ вальцевание"),
    ROLLING_PR("СПЦФ ПР вальцевание"),

    //Листогиб
    SHEET_BENDING_WORKER_HOURS("СПЦФ трудочаса рабочего листогиб"),
    SHEET_BENDING_SINGLE("СПЦФ ПР гиб одинарный(один гиб - одна деталь): Сталь, аллюминий, нержавейка"),

    // Лентопил
    BANDSAW_WORKER_HOURS("СПЦФ трудочаса рабочего лентопил"),
    BANDSAW_PR("СПЦФ ПР рез деталей на лентопильном станке: Сталь,Латунь,Медь,Аллюминий"),

    // Другие
    STUD("СПЦФ Шпилька"),
    DRILLING("СПЦФ ПР сверление отверстий, зенкование, нарезка резьбы");


    private final String nomenclature;

    TypeOfOperations(String nomenclature) {
        this.nomenclature = nomenclature;
    }

    public String getNomenclature() {
        return nomenclature;
    }

    public static TypeOfOperations fromNomenclature(String nomenclature) {
        for (TypeOfOperations type : values()) {
            if (type.nomenclature.equalsIgnoreCase(nomenclature.trim())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown nomenclature: " + nomenclature);
    }
}
