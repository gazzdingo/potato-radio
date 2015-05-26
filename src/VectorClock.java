import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Ben on 25/05/15.
 */
public class VectorClock extends HashMap<String, Integer> implements Serializable{

    // Unique Serial.
    private static final long serialVersionUID = 6668164199894268488L;


    public void incrementClock(String pUnit)
    {
        // If we have it in the vector, increment.
        if (this.containsKey(pUnit))
        {
            this.put(pUnit, this.get(pUnit).intValue() + 1);
        }
        // Else, store with value 1 (starts at 0, +1).
        else
        {
            this.put(pUnit, 1);
        }
    }

    /**
     * GUI operation, returns the IDs in some neat order.
     *
     * @return The IDs of the elements in the Clock.
     */
    public String[] getOrderedIDs()
    {
        String[] lResult = new String[this.size()];

        lResult = this.keySet().toArray(lResult);

        Arrays.sort(lResult);

        return lResult;
    }

    /**
     * GUI operation, returns the values in some neat order.
     *
     * @return The Values of the elements in the Clock.
     */
    public Integer[] getOrderedValues()
    {
        Integer[] lResult = new Integer[this.size()];
        String[] lKeySet  = this.getOrderedIDs();

        int i = 0;
        for (String lKey : lKeySet)
        {
            lResult[i] = this.get(lKey);
            i++;
        }

        return lResult;
    }

    @Override
    public Integer get(Object key)
    {
        Integer lResult = super.get(key);

        if (lResult == null)
            lResult = 0;

        return lResult;
    }

    @Override
    public VectorClock clone()
    {
        return (VectorClock) super.clone();
    }

    @Override
    public String toString()
    {
        String[] lIDs		= this.getOrderedIDs();
        Integer[] lRequests = this.getOrderedValues();

        String lText = "(";

        for (int i = 0; i < lRequests.length; i++)
        {
            lText += lIDs[i];
            lText += " = ";
            lText += lRequests[i].toString();

            if (i + 1 < lRequests.length)
            {
                lText += ", ";
            }
        }

        lText += ")";

        return lText;
    }

    /**
     * VectorClock merging operation. Creates a new VectorClock with the maximum for
     * each element in either clock. Used in Buffer and Process to manipulate clocks.
     *
     * @param pOne - First Clock being merged.
     * @param pTwo - Second Clock being merged.
     *
     * @return A new VectorClock with the maximum for each element in either clock.
     */
    public static VectorClock max(VectorClock pOne, VectorClock pTwo)
    {
        // Create new Clock.
        VectorClock lResult = new VectorClock();

        // Go over all elements in clock One, put them in the new clock.
        for (String lEntry : pOne.keySet())
        {
            lResult.put(lEntry, pOne.get(lEntry));
        }

        // Go over all elements in clock Two,
        for (String lEntry : pTwo.keySet())
        {
            // Insert the Clock Two value if it is not present in One, or if it is higher.
            if (!lResult.containsKey(lEntry) || lResult.get(lEntry) < pTwo.get(lEntry))
            {
                lResult.put(lEntry, pTwo.get(lEntry));
            }
        }

        // Return the merged clock.
        return lResult;
    }



}
