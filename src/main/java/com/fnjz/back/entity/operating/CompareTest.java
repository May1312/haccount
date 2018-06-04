package com.fnjz.back.entity.operating;

import java.util.ArrayList;
import java.util.Collections;

public class CompareTest {
    public static void main(String[] args) {
        ArrayList<IncomeTypeEntity> IncomeTypeEntitys = new ArrayList<>();
        IncomeTypeEntitys.add(new IncomeTypeEntity("7","","","","0",7,0));
        IncomeTypeEntitys.add(new IncomeTypeEntity("8","","","","0",8,0));
        IncomeTypeEntitys.add(new IncomeTypeEntity("4","","","","0",4,0));
        IncomeTypeEntitys.add(new IncomeTypeEntity("1","","","","0",4,0));
        IncomeTypeEntitys.add(new IncomeTypeEntity("2","","","","0",2,0));
        IncomeTypeEntitys.add(new IncomeTypeEntity("3","","","","0",3,0));
        IncomeTypeEntitys.add(new IncomeTypeEntity("5","","","","0",4,0));
        IncomeTypeEntitys.add(new IncomeTypeEntity("6","","","","0",6,0));


        System.out.println("排序前");
        for (IncomeTypeEntity incomeTypeEntity : IncomeTypeEntitys) {

            System.out.println("id==============="+incomeTypeEntity.getId()+"顺序================"+incomeTypeEntity.getPrority());
        }

        Collections.sort(IncomeTypeEntitys);

        System.out.println("排序后");

        for (int i = 0; i < IncomeTypeEntitys.size(); i++) {
            IncomeTypeEntity incomeTypeEntity = IncomeTypeEntitys.get(i);
            int pri = i+1;
            System.out.println("id==============="+incomeTypeEntity.getId()+"顺序================"+pri);
        }


    }
}
