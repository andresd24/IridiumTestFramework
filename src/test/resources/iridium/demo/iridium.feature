Feature: Iridum Web Service
  To allow testing of SOAP API

  Scenario Outline: findServiceProviderProfile test scenario
    Given the method exists findServiceProviderProfile exists the Iridiums service 
    And a request is sent to findServiceProviderProfile with iwsUsername '<iwsUsername>', signature '<signature>', serviceProviderAccountNumber '<serviceProviderAccountNumber>' and timestamp '<timestamp>'
    Then the result must contain the expected account number '<serviceProviderAccountNumber>' name '<accountName>' 
    
	[autodatagen]
    
