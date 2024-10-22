package com.jl.newshubapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int q = scanner.nextInt();

        ArrayList<Integer> list = new ArrayList<>();
        for(int i = 1;i<n+1;i++){
            list.add(i);
        }
        while(q >=0){
            int a = scanner.nextInt();
            int b = scanner.nextInt();
            int op = scanner.nextInt();
            q--;
            list.remove(a);
            int index = list.indexOf(b);
            if(op == 1){
                list.add(index+1,a);
            }else{
                list.add(index,a);
            }
        }
        List<String> stringList = new ArrayList<>();
        for(Integer i : list){
            stringList.add(i.toString());
        }
        String  res = String.join(" ",stringList);
        System.out.println(res);


    }
}
