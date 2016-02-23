Feature: Iridum Web Service
  To allow testing of SOAP API

  Scenario Outline: findServiceProviderProfile test scenario
    Given that the Iridiums service is up and running
    And a request is sent to web service 'findServiceProviderProfile' with iwsUsername '<iwsUsername>', signature '<signature>', serviceProviderAccountNumber '<serviceProviderAccountNumber>' and timestamp '<timestamp>'
    Then the result must contain the expected account number '<serviceProviderAccountNumber>' and account name '<accountName>' 
   
   	#web services:  findServiceProviderProfile,  mockServiceNotUsed
   
	[autodatagen]



