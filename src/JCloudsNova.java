import com.google.common.collect.ImmutableSet;
import com.google.common.io.Closeables;
import com.google.inject.Module;
import org.jclouds.ContextBuilder;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.nova.v2_0.domain.Image;
import org.jclouds.openstack.nova.v2_0.features.ImageApi;
import org.jclouds.openstack.nova.v2_0.domain.Flavor;
import org.jclouds.openstack.nova.v2_0.features.FlavorApi;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroup;
import org.jclouds.openstack.nova.v2_0.extensions.SecurityGroupApi;
import org.jclouds.openstack.nova.v2_0.options.CreateServerOptions;
import org.jclouds.openstack.nova.v2_0.domain.ServerCreated;
import java.io.Closeable;
import java.io.IOException;
import java.util.Set;

public class JCloudsNova implements Closeable {
    private final NovaApi novaApi;
    private final Set<String> regions;

    public static void main(String[] args) throws IOException {
        JCloudsNova jcloudsNova = new JCloudsNova();

        try {
        	
            jcloudsNova.listServers();
            jcloudsNova.createServer();
            jcloudsNova.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jcloudsNova.close();
        }
    }

    public JCloudsNova() {
        //Iterable<Module> modules = ImmutableSet.<Module>of(new SLF4JLoggingModule());

        String provider = "openstack-nova";
        String osTenantName = "pt-27168"; //replace with appropriate tenant name
        String osUsername = "chandelk@utas.edu.au"; //replace with appropriate user name
        String identity = String.format("%1$s:%2$s", osTenantName, osUsername); //concat osTenantName and osUsername with a ':'
        
        String credential = "YTgwNTM1MzYxZjY0NTY0";//add credential

        //getting object to access nectar cloud apis
        novaApi = ContextBuilder.newBuilder(provider)
                .endpoint("https://keystone.rc.nectar.org.au:5000/v2.0/")
                .credentials(identity, credential)
                .buildApi(NovaApi.class);
                //.modules(modules)
                
        
        regions = novaApi.getConfiguredRegions();
        //novaApi.getser
    }
    private void createServer(){
    	String nameser="testServerabc";
        ImageApi imageApi=novaApi.getImageApi("Melbourne");
        FlavorApi flavors=novaApi.getFlavorApi("Melbourne");
        String imageid="";
        //change name of the image to your image name
        String imgname=new String("NeCTAR Ubuntu 14.04 (Trusty) amd64e");
        //getting the image id if name is known
        System.out.println("imagename");
        for(Image img:imageApi.listInDetail().concat())
        {
        	if(imgname.equals(img.getName())){

                	System.out.println(img.getId()+ " "+img.getName());
                	imageid=img.getId();
        	}
           
        }
        System.out.println("\n\n\n flavorename");
        //get list of flavors
        String flavorid="0";
        for(Flavor img1:flavors.listInDetail().concat())
        {
               System.out.println(img1.getId()+ " "+img1.getName()+" "+img1.getDisk());
               //image size greater than 15G
               if((img1.getDisk()>15)&&(img1.getVcpus()<3)){
            	   flavorid=img1.getId();
            	   
                   break;
               }
        }
        
  
       
        //flavor ="13000ccd-6a24-4bc5-9520-743707f8c0a2";
       
       SecurityGroupApi secureApi=novaApi.getSecurityGroupApi("Melbourne").get();
       // script you want to run at the start of your VM instance
       String userdata="#!/bin/bash \n  sudo su ubuntu \n cd /mirror/mpiu/ \n sudo ls -l >test";

    		   
//     System.out.println("\n\n"+secureApi.list());
        //Replace YYYYY with your key pair name
       CreateServerOptions options1= CreateServerOptions.Builder.keyPairName("mpicluster").
    		   availabilityZone("tasmania").securityGroupNames("SSH").userData(userdata.getBytes()); 
       
       ServerApi serverApi = novaApi.getServerApi("Melbourne");
       //create server
      ServerCreated screa=serverApi.create(nameser,imageid,flavorid,options1);
      System.out.println("serverid "+screa.getId());
       //to get status
        String status=serverApi.get(screa.getId()).getStatus().toString();
      //System.out.println("\n\n Status:"+status);
     // System.out.println("\n\n\n  "+serverApi.get("f9e4b37e-228f-46fc-9e85-//c1ae9a77f560").getStatus()+" "+serverApi.get("f9e4b37e-228f-46fc-9e85-c1ae9a77f560").getName());
      
    }
    private void listServers() {
       for (String region : regions) {
           ServerApi serverApi = novaApi.getServerApi(region);

            System.out.println("Servers in " + region);

            for (Server server : serverApi.listInDetail().concat()) {
                System.out.println("  " + server.getStatus()+" "+server.getName()+" "+server.getAvailabilityZone());
            }
        }
    }

    public void close() throws IOException {
        Closeables.close(novaApi, true);
    }
}
