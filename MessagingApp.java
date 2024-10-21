import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import java.util.Date;



import java.io.*;
import java.util.*;

// User class to represent a user
class User implements Serializable {
   private String username;
   private String email;

   public User(String username, String email) {
      this.username = username;
      this.email = email;
   }

   public String getUsername() {
      return username;
   }

    // Add getters and setters for email
}
// Message class to represent a single message
class Message implements Serializable {
   private String sender;
   private String recipient;
   private String content;
   private Date timestamp;

   public Message(String sender, String recipient, String content, Date timestamp) {
      this.sender = sender;
      this.recipient = recipient;
      this.content = content;
      this.timestamp = timestamp;
   }

   public String getSender() {
      return sender;
   }

   public String getRecipient() {
      return recipient;
   }

   public Date getTimestamp() {
      return timestamp;
   }
}

// CloudStorage interface for cloud operations
interface CloudStorage {
   void saveUser(User user);
   User getUser(String username);
   void saveMessage(Message message);
   List<Message> getConversationHistory(String user1, String user2);
   void saveContact(String user, String contact);
   List<String> getContacts(String user);
}

// Implement CloudStorage for Amazon S3
class AmazonS3CloudStorage implements CloudStorage {
   private final S3Client s3;
   private final String bucketName;

   public AmazonS3CloudStorage(String bucketName, Region region) {
      this.s3 = S3Client.builder()
             .region(region)
             .credentialsProvider(ProfileCredentialsProvider.create())
             .build();
      this.bucketName = bucketName;
   }

   @Override
   public void saveUser(User user) {
      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(baos);
         oos.writeObject(user);
         oos.close();
      
         PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key("users/" + user.getUsername())
                .build();
      
         s3.putObject(request, RequestBody.fromBytes(baos.toByteArray()));
      } catch (IOException e) {
         throw new RuntimeException("Error saving user", e);
      }
   }

   @Override
   public User getUser(String username) {
      try {
         GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key("users/" + username)
                .build();
      
         ResponseInputStream<GetObjectResponse> response = s3.getObject(request);
         ObjectInputStream ois = new ObjectInputStream(response);
         return (User) ois.readObject();
      } catch (IOException | ClassNotFoundException e) {
         throw new RuntimeException("Error retrieving user", e);
      }
   }

   @Override
   public void saveMessage(Message message) {
      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(baos);
         oos.writeObject(message);
         oos.close();
      
         String key = String.format("messages/%s/%s/%d", 
            message.getSender(), message.getRecipient(), message.getTimestamp().getTime());
      
         PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
      
         s3.putObject(request, RequestBody.fromBytes(baos.toByteArray()));
      } catch (IOException e) {
         throw new RuntimeException("Error saving message", e);
      }
   }

   @Override
   public List<Message> getConversationHistory(String user1, String user2) {
      List<Message> messages = new ArrayList<>();
      String prefix = "messages/" + user1 + "/" + user2;
   
      ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
             .bucket(bucketName)
             .prefix(prefix)
             .build();
   
      ListObjectsV2Response listResponse = s3.listObjectsV2(listRequest);
   
      for (S3Object s3Object : listResponse.contents()) {
         GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Object.key())
                .build();
      
         try (ResponseInputStream<GetObjectResponse> response = s3.getObject(getRequest);
             ObjectInputStream ois = new ObjectInputStream(response)) {
            messages.add((Message) ois.readObject());
         } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error retrieving message", e);
         }
      }
   
      return messages;
   }

   @Override
   public void saveContact(String user, String contact) {
      String key = "contacts/" + user + "/" + contact;
      PutObjectRequest request = PutObjectRequest.builder()
             .bucket(bucketName)
             .key(key)
             .build();
   
      s3.putObject(request, RequestBody.fromBytes(new byte[0]));
   }

   @Override
   public List<String> getContacts(String user) {
      List<String> contacts = new ArrayList<>();
      String prefix = "contacts/" + user + "/";
   
      ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
             .bucket(bucketName)
             .prefix(prefix)
             .build();
   
      ListObjectsV2Response listResponse = s3.listObjectsV2(listRequest);
   
      for (S3Object s3Object : listResponse.contents()) {
         String key = s3Object.key();
         contacts.add(key.substring(prefix.length()));
      }
   
      return contacts;
   }
}

// Usage example
public class MessagingApp {
   public static void main(String[] args) {
      CloudStorage storage = new AmazonS3CloudStorage("testcontactbucket", Region.US_EAST_2);
   
      // Save a user
      User user = new User("john_doe", "john@example.com");
      storage.saveUser(user);
   
      // Retrieve a user
      User retrievedUser = storage.getUser("john_doe");
   
      // Save a message
      Message message = new Message("john_doe", "jane_doe", "Hello, Jane!", new Date());
      storage.saveMessage(message);
   
      // Get conversation history
      List<Message> history = storage.getConversationHistory("john_doe", "jane_doe");
   
      // Save a contact
      storage.saveContact("john_doe", "jane_doe");
   
      // Get contacts
      List<String> contacts = storage.getContacts("john_doe");
   }
}