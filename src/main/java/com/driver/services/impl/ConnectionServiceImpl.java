package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    UserRepository userRepository2;
    @Autowired
    ServiceProviderRepository serviceProviderRepository2;
    @Autowired
    ConnectionRepository connectionRepository2;

    @Override
    public User connect(int userId, String countryName) throws Exception{
          User user=userRepository2.findById(userId).get();
          ServiceProvider serviceProvider=null;
          List<ServiceProvider> serviceProviderList=user.getServiceProviderList();

          for(ServiceProvider service:serviceProviderList){
              if(service.getCountryList().contains(countryName)){
                  serviceProvider=service;
                  break;
              }
          }
          if(serviceProvider==null){
              throw new Exception("Invalid country name");
          }
          user.setMaskedIp(countryName);
          Connection connection=new Connection();
          connection.setServiceProvider(serviceProvider);
          connection.setUser(user);
          connectionRepository2.save(connection);
          user.setConnected(true);
          serviceProvider.getConnectionList().add(connection);
          user.getConnectionList().add(connection);

          serviceProviderRepository2.save(serviceProvider);

          return userRepository2.save(user);

    }
    @Override
    public User disconnect(int userId) throws Exception {
      User user=userRepository2.findById(userId).get();
      user.setConnected(false);
      user.setMaskedIp(null);
      return userRepository2.save(user);

    }
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {
             User sender=userRepository2.findById(senderId).get();
             User receiver=userRepository2.findById(receiverId).get();

             if(sender.getCountry().equals(receiver.getCountry())){

             }else{
                 if(sender.getConnected()!=receiver.getConnected()){
                     throw new Exception("Both are not from same country and one of them not connected to any vpn");
                 }
                 else{
                     if(sender.getMaskedIp()!= receiver.getMaskedIp()){
                         throw new Exception("Both are not connected to same country vpn");
                     }
                 }

             }
             userRepository2.save(receiver);
             return userRepository2.save(sender);
    }
}
