package uml_navigate;

import java.util.*;

/**
 *Iterate string from "N" and the set all of strings is prefix free.
 * So, the last character can be "N" to "Z", others are "A" to "M".
 * */
public class KeyIterator implements Iterator<String> {
    private LinkedList<Character> charArray = new LinkedList<Character>(Arrays.asList('N'));

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public String next() {
        StringBuilder builder = new StringBuilder();
        for(Character c:charArray)
            builder.append(c);
        if(charArray.getLast() != 'Z')
            charArray.set(charArray.size()-1, (char) (charArray.getLast()+1));
        else
        {
            charArray.set(charArray.size()-1,'N');
            boolean carry = false;
            for(int i=charArray.size()-2;i >= 0;i--)
            {
                if(charArray.get(i) != 'M')
                {
                    carry = true;
                    charArray.set(i, (char) (charArray.get(i)+1));
                    break;
                }
                else
                {
                    charArray.set(i, 'A');
                }
            }
            if(!carry)
            {
                charArray.addFirst('A');
            }
        }
        return builder.toString();
    }
}
