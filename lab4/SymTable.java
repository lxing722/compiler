
import java.util.*;

/**
 * A class for Symbol Table
 */
public class SymTable {
    private LinkedList<HashMap<String, SymInfo>> list;
    /**
     * Create a Symbol Table with one empty scope
     */
    public SymTable() {
        list = new LinkedList<HashMap<String, SymInfo>>();
        HashMap<String, SymInfo> map = new HashMap<String, SymInfo>();
        list.addFirst(map);
    }

    /**
     * Add a declaration (i.e. a pair [name,sym]) in the inner scope
     */
    public void addDecl(String name, SymInfo sym) throws NullPointerException, DuplicateSymException, EmptySymTableException {
        if(list.isEmpty()){
            throw new EmptySymTableException();
        }
        else if(name == null || sym == null){
            throw new NullPointerException();
        }
        else{
            if(list.getFirst().containsKey(name)){
                throw new DuplicateSymException();
            }
            else{
                list.getFirst().put(name, sym);
            }
        }
    }

    /**
     * Add a new inner scope
     */
    public void addScope() {
        HashMap<String, SymInfo> map = new HashMap<String, SymInfo>();
        list.addFirst(map);
    }

    /**
     * Lookup for 'name' in the inner scope
     */
    public SymInfo lookupLocal(String name) throws EmptySymTableException {
        if(list.isEmpty()){
            throw new EmptySymTableException();
        }
        else{
            if(list.peekFirst().containsKey(name)){
                return list.peekFirst().get(name);
            }
            else{
                return null;
            }
        }
    }

    /**
     * Lookup for 'name' sequentially in all scopes from inner to outer
     */
    public SymInfo lookupGlobal(String name)  throws EmptySymTableException {
        if(list.isEmpty()){
            throw new EmptySymTableException();
        }
        else{
            Iterator<HashMap<String, SymInfo>> iter = list.iterator();
            while(iter.hasNext()){
                HashMap<String, SymInfo> map = iter.next();
                if(map.containsKey(name)){
                    return map.get(name);
                }
            }
            return null;
        }
    }

    /**
     * Remove the inner scope
     */
    public void removeScope() throws EmptySymTableException {
        if(list.isEmpty()){
            throw new EmptySymTableException();
        }
        else{
            list.removeFirst();
        }
    }

    /**
     * Print the Symbol Table on System.out
     */
    public void print() {
        System.out.print("\nSym table\n");
        Iterator<HashMap<String, SymInfo>> iter = list.iterator();
        while(iter.hasNext()){
            System.out.print("{");
            Iterator<Map.Entry<String, SymInfo>> itr = iter.next().entrySet().iterator();
            if(itr.hasNext()){
                Map.Entry<String, SymInfo> entry1 = (Map.Entry<String, SymInfo>) itr.next();
                System.out.print(entry1.getKey() + "=" + entry1.getValue().toString());
                while(itr.hasNext()){
                    Map.Entry<String, SymInfo> entry2 = (Map.Entry<String, SymInfo>) itr.next();
                    System.out.print(", ");
                    System.out.print(entry2.getKey() + "=" + entry2.getValue().toString());
                }
            }
            System.out.print("}\n");
        }
    }  
}
