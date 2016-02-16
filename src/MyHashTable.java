/**
 * 08-722 Data Structures for Application Programmers.
 *
 * Homework Assignment 4
 * HashTable Implementation with linear probing
 *
 * Andrew ID: mujingz
 * @author Mujing Zhou
 */
public class MyHashTable implements MyHTInterface {
    private final double LOADFACTOR = 0.5;
    private final int HAHSINGCONSTANT = 27;
    private final int DEFAULTCAPACITY = 10;

    private int tableLenght;
    private DataItem[] hashArray;
    private static final DataItem DELETED = new DataItem("DEL",0);
    private int actualSize;
    private int numOfCollisions;

    // TODO implement constructor with no initial capacity
    public MyHashTable(){
        this.tableLenght = DEFAULTCAPACITY;
        this.actualSize = 0;
        this.hashArray = new DataItem[tableLenght];
    }

    // TODO implement constructor with initial capacity
    public MyHashTable(int initialCapacity){
        if (initialCapacity<=0) throw new RuntimeException();
        this.tableLenght = initialCapacity;
        this.actualSize = 0;
        this.hashArray = new DataItem[tableLenght];
    }
    // TODO implement required methods

    /**
     * Instead of using String's hashCode, you are to implement your own here.
     * You need to take the table length into your account in this method.
     *
     * In other words, you are to combine the following two steps into one step.
     * 1. converting Object into integer value
     * 2. compress into the table using modular hashing (division method)
     *
     * Helper method to hash a string for English lowercase alphabet and blank,
     * we have 27 total. But, you can assume that blank will not be added into
     * your table. Refer to the instructions for the definition of words.
     *
     * For example, "cats" : 3*27^3 + 1*27^2 + 20*27^1 + 19*27^0 = 60,337
     *
     * But, to make the hash process faster, Horner's method should be applied as follows;
     *
     * var4*n^4 + var3*n^3 + var2*n^2 + var1*n^1 + var0*n^0 can be rewritten as
     * (((var4*n + var3)*n + var2)*n + var1)*n + var0
     *
     * Note: You must use 27 for this homework.
     *
     * However, if you have time, I would encourage you to try with other
     * constant values than 27 and compare the results but it is not required.
     * @param input input string for which the hash value needs to be calculated
     * @return int hash value of the input string
     */
    private int hashFunc(String input) {
        // TODO implement this
//        input = "cats";

        int result = 0;
        if (input==null||input.length()==0) return -1;
        int size = input.length();
        for (int i=0;i<size;i++){
            char tempChar = input.charAt(i);
//            if (tempChar<'a'||tempChar>'z'){
//                return -1;
//            }
            result=((input.charAt(i)-'a'+1)+ HAHSINGCONSTANT *result)%tableLenght;
        }
//        System.out.println("before is "+result);
        result = result%tableLenght;
//        System.out.println("hashvalue of "+input+" is "+result+" tableLength is "+tableLenght);
        return result;
    }

    /**
     * doubles array length and rehash items whenever the load factor is reached.
     */
    private void rehash() {
        // TODO implement this
//        System.out.println("before rehashing");
//        display();
        int tempLength = tableLenght;
        tableLenght = findNewTableLenght(tableLenght);
        System.out.println("Rehashing "+actualSize+" items, new size is "+tableLenght);
//        System.out.println("new hashed length is "+tableLenght);

        MyHashTable tempHashtable = new MyHashTable(tableLenght);
        for (int i=0;i<tempLength;i++){
            if (hashArray[i]!=null&&hashArray[i]!=DELETED){
                int oldFrequency = hashArray[i].frequency;

                for (int j=0;j<oldFrequency;j++) {
//                    System.out.println("reinserting "+hashArray[i].value);
                    tempHashtable.insert(hashArray[i].value);
                }
            }
        }

        this.hashArray = tempHashtable.hashArray;
        this.actualSize = tempHashtable.actualSize;
        this.numOfCollisions = tempHashtable.numOfCollisions;
//        System.out.println("after rehashing rehashing");
//        display();
    }

    private int findNewTableLenght(int tableLenght){
        int start = 2*tableLenght;
        while(!isPrime(start)){
            start++;
        }

        return start;
    }

    private boolean isPrime(int input){

        for (int i=2;i<Math.sqrt(input);i++){
            if (input%i==0) return false;
        }
        return true;
    }

    @Override
    public void insert(String value) {
//        System.out.println("table Lenght"+tableLenght);
//        System.out.println("actual size"+actualSize);
        int hashVal = hashFunc(value);
        int tempHashVal = hashVal;
//        if (hashVal==-1) return;
        if (hashArray[hashVal]!=null){
            if (!contains(value)){
//                System.out.println(value+"collide ");
//                numOfCollisions++;
                while(hashArray[hashVal]!=null){
                    if (hashFunc(hashArray[hashVal].value)==tempHashVal){
                        numOfCollisions++;
                        break;
                    }
                    hashVal++;
                    hashVal=hashVal%tableLenght;
                }
            }

        }
//        boolean needRehashing = false;
//        if ((double)tableLenght*LOAD_FACTOR<=(double)(actualSize)) {
////            System.out.println("rehashing");
//            needRehashing = true;
////            rehash();
//        }

        // already contains
        if (contains(value)) {

            DataItem findItem = find(value,hashVal);
            findItem.frequency++;
        }
        // does not contain
        else {
            addNewItem(value,hashVal);
            actualSize++;
        }

//        if (needRehashing==true){
//            rehash();
//        }
        if ((double)tableLenght*LOADFACTOR<(double)(actualSize)) {
//            System.out.println("rehashing");
//            needRehashing = true;
            rehash();
        }
    }

    public void addNewItem(String value,int hashVal){


        while (hashArray[hashVal]!=null){
            hashVal++;
            hashVal = hashVal%tableLenght;
        }
        hashArray[hashVal] = new DataItem(value,1);

    }

    public DataItem find(String value,int hashVal){

        while (hashArray[hashVal]!=null){
            if (hashArray[hashVal].value.equals(value)){
                return hashArray[hashVal];
            }
            hashVal++;
            hashVal = hashVal%tableLenght;
        }
        return null;
    }

    @Override
    public int size() {
        return actualSize;
    }

    @Override
    public void display() {
        for (int i=0;i<tableLenght-1;i++){
            if (hashArray[i]==null) {
                System.out.print("**"+" ");
            }
            else if (hashArray[i].value.equals("DEL")){
                System.out.print("#DEL#"+" ");
            }
            else {
                System.out.printf("["+hashArray[i].value+", "+hashArray[i].frequency+"]"+" ");
            }
        }
        if (hashArray[tableLenght-1]==null) {
            System.out.print("**");
        }
        else if (hashArray[tableLenght-1].value.equals("DEL")){
            System.out.print("#DEL#");
        }
        else {
            System.out.printf("["+hashArray[tableLenght-1].value+", "+hashArray[tableLenght-1].frequency+"]");
        }
        System.out.println();
    }

    @Override
    public boolean contains(String key) {
        int hashVal = hashFunc(key);
//        if (hashVal == -1) return false;
//        System.out.println("hashVal is "+hashVal+"of "+key);
        while (hashArray[hashVal]!=null){
            if (hashArray[hashVal].value.equals(key)){
                return true;
            }
            hashVal++;
            hashVal = hashVal%tableLenght;
        }
        return false;
    }

    @Override
    public int numOfCollisions() {
        return numOfCollisions;
    }

    @Override
    public int hashValue(String value) {
        return hashFunc(value);
    }

    @Override
    public int showFrequency(String key) {
        int hashVal = hashFunc(key);
//        if (hashVal==-1) return 0;
        if (contains(key)) {

            DataItem temp = find(key,hashVal);
            return temp.frequency;
        }
        else return 0;
    }

    @Override
    public String remove(String key) {
        int hashVal = hashFunc(key);
//        if (hashVal==-1) return null;
        while(hashArray[hashVal]!=null){
            if (hashArray[hashVal].value.equals(key)){
                String result = hashArray[hashVal].value;
                hashArray[hashVal] = DELETED;
                actualSize--;
                return result;
            }
            hashVal++;
            hashVal=hashVal%tableLenght;
        }
        return null;
    }

    /**
     * private static data item nested class.
     */
    private static class DataItem {
        /**
         * String value.
         */
        private String value;
        /**
         * String value's frequency.
         */
        private int frequency;

        public DataItem(){

        }

        public DataItem(String value,int frequency){
            this.value = value;
            this.frequency = frequency;
        }
        // TODO implement constructor and methods
    }

}
