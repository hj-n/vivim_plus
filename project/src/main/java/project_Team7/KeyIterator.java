package project_Team7;


import java.util.*;

/**
 * This class is for generating keys for key-mapping.
 * Iterate string from "N" and the set all of strings is prefix free.
 * So, the last character can be "N" to "Z", others are "A" to "M".
 * */
public class KeyIterator implements Iterator<String> {
    private LinkedList<Character> nextKey = new LinkedList<Character>(Arrays.asList('N'));

    @Override
    public boolean hasNext() {
        return true;
    }

    /**
     * Generates prefix-free key. The sequence will be like"
     * N, O, P, .... , X, Y, Z, AN, AO, .... , AZ, BN, BO ...
     * @return generated next prefix-free key in the sequence
     */
    @Override
    public String next() {
        StringBuilder builder = new StringBuilder();
        for(Character c:nextKey)
            builder.append(c);
        if(nextKey.getLast() != 'Z')
            nextKey.set(nextKey.size()-1, (char) (nextKey.getLast()+1));
        else
        {
            nextKey.set(nextKey.size()-1,'N');
            boolean carry = false;
            for(int i=nextKey.size()-2;i >= 0;i--)
            {
                if(nextKey.get(i) != 'M')
                {
                    carry = true;
                    nextKey.set(i, (char) (nextKey.get(i)+1));
                    break;
                }
                else
                {
                    nextKey.set(i, 'A');
                }
            }
            if(!carry)
            {
                nextKey.addFirst('A');
            }
        }
        return builder.toString();
    }
}

