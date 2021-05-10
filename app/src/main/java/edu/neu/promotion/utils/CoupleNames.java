package edu.neu.promotion.utils;

import android.content.res.Resources;

import java.util.HashSet;
import java.util.Scanner;

import edu.neu.promotion.R;

public class CoupleNames {

    private static CoupleNames instance;

    public static CoupleNames getInstance(Resources resources) {
        if (instance == null) {
            instance = new CoupleNames();
            Scanner scanner = new Scanner(resources.openRawResource(R.raw.couple_names));
            while (scanner.hasNext()) {
                instance.coupleNames.add(scanner.nextLine());
            }
            scanner.close();
        }
        return instance;
    }

    private final HashSet<String> coupleNames;

    public CoupleNames() {
        coupleNames = new HashSet<>();
    }

    public boolean match(String name) {
        return coupleNames.contains(name);
    }

    public String getShortName(String fullName) {
        String firstName;
        switch (fullName.length()) {
            case 0:
            case 1:
            case 2:
                //如果名字长度小于2个字，则返回原始名字
                return fullName;
            case 3:
                //如果名字长度等于3个字，若是复姓，则返回姓，否则返回后两个字
                firstName = fullName.substring(0, 2);
                if (match(firstName)) {
                    return firstName;
                }
                else {
                    return fullName.substring(1);
                }
            default:
                firstName = fullName.substring(0, 2);
                if (match(firstName)) {
                    return fullName.substring(2, 4);
                }
                else {
                    return fullName.substring(1, 3);
                }
        }
    }
}
