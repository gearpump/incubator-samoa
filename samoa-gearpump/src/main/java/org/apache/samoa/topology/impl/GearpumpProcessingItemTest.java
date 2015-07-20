package org.apache.samoa.topology.impl;

/**
 * Created by keweisun on 7/20/2015.
 */
public class GearpumpProcessingItemTest {

    public static void main(String[] args) {
        ProcessorTest pt = new ProcessorTest();
        pt.setI(3);
        pt.setS("ptet");
        System.out.println("pt: " + pt.getI() + pt.getS());
        GearpumpProcessingItem pi = new GearpumpProcessingItem(pt, 1);
        System.out.println("parallelism: " + pi.getParallelism());
        if (pi.getProcessor() != null){
            System.out.println("processor is not null");
            System.out.println("processor: " + pi.getProcessor().getClass().getSimpleName());
        } else {
            System.out.println("processor is null");
        }

//        SerializableTest st = new SerializableTest();
//        st.setI(1);
//        st.setS("st");
//
//        ProcessorWrapperTest pi = new ProcessorWrapperTest(pt);
//        pi.setSt(st);
//        if (pi.getPt() != null){
//            System.out.println("processor is not null");
//            System.out.println("processor: " + pi.getPt().getS());
//        } else {
//            System.out.println("processor is null");
//        }
//        if (pi.getSt() != null){
//            System.out.println("st is not null");
//            System.out.println("st: " + pi.getSt().getS());
//        } else {
//            System.out.println("st is null");
//        }

        System.out.println("after serializable==================");

        byte[] bytes = GearpumpSamoaUtils.objectToBytes(pi);
        GearpumpProcessingItem piFromBytes =
                (GearpumpProcessingItem) GearpumpSamoaUtils.bytesToObject(bytes);
        if (piFromBytes.getProcessor() != null){
            System.out.println("processor is not null");
            System.out.println("processor: " + piFromBytes.getProcessor().getClass().getSimpleName());
        } else {
            System.out.println("processor is null");
        }

    }
}
