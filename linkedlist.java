public class linkedlist {
    Node head; 
    class Node
    {
        
        String data;
        Node next;

        Node(String data)
        {
            this.data = data;
            this.next = null;
        }
    }
public void addFirst(String data)
{
    Node newnode = new Node(data);
    if(head == null)
    {
        head = newnode;
        return;
    }
    newnode.next = head;
    head = newnode;
}
public void addLast(String data)
{
    Node newnode = new Node(data);
    if(head == null)
    {
        head = newnode;
        return;
    }
    Node currNode = head;
    while(currNode != null)
    {
        currNode = currNode.next;
    }

    currNode.next = newnode;
}
public void printlist()
{
    if(head == null)
    {
        System.out.println("list is null");
    }
    Node currNode = head;
    while(currNode != null)
    {
        System.out.print(currNode.data+" ->");
        currNode = currNode.next;
    }
    System.out.println("NULL");
}
public static void main(String[] args)
{
    linkedlist list = new linkedlist();
    list.addFirst(" a");
    list.addFirst("is");
    list.printlist();
}    
}
