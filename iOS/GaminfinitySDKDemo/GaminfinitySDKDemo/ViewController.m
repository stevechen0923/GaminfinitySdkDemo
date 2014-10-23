//
//  ViewController.m
//  GaminfinitySDKDemo
//
//  Created by funtown on 2014/8/12.
//  Copyright (c) 2014年 Gaminfinity. All rights reserved.
//

#import "ViewController.h"

//NSString *const GAMINFINITY_SERVER_URL = @"http://219.87.94.74/sns/new_bind_uuid.php";
NSString *const GAMINFINITY_SERVER_URL = @"https://ests-sdk.wartown.com.tw/sns/new_bind_uuid.php";

@interface ViewController ()

@end

@implementation ViewController
{
    GaminfinitySDK* gaminfinitySdk;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
    
    //Gaminfinity SDK init
    gaminfinitySdk = [[GaminfinitySDK alloc]init];
    gaminfinitySdk.delegate = self;
    
    // check cache if FB already login
    if (FBSession.activeSession.state == FBSessionStateCreatedTokenLoaded){
        [FBSession openActiveSessionWithReadPermissions:nil allowLoginUI:NO
            completionHandler:
                ^(FBSession *session, FBSessionState state, NSError *error){
                    [self sessionStateChanged:session state:state error:error];
                }];
    }
    //update Fb login button text
    [self updateUI];
}

- (void)sessionStateChanged:(FBSession *)session state:(FBSessionState) state error:(NSError *)error
{
    NSLog(@"sessionStateChanged()");
    if (error){
        NSLog(@"error or login cenceled!");
        // handel error if needed.
        [self facebookLogout];
    }else{
        if (state == FBSessionStateOpen){
            NSLog(@"FB Session opened");
            FBRequest *me = [FBRequest requestForMe];
            [me startWithCompletionHandler:^(FBRequestConnection *connection, id result,
                                             NSError *error) {
                NSDictionary<FBGraphUser> *my = (NSDictionary<FBGraphUser> *) result;
                NSLog(@"My First Name: %@", my.first_name);
                NSLog(@"My Id: %@", my.objectID);
            }];
            
            //call Gaminfinity SDK
            NSString *fbAccessToken = [FBSession activeSession].accessTokenData.accessToken;
            [gaminfinitySdk getAccountId:fbAccessToken ServerUrl:GAMINFINITY_SERVER_URL];
        }else if (state == FBSessionStateClosed || state == FBSessionStateClosedLoginFailed){
            NSLog(@"Session closed");
        }
    }
    [self updateUI];
}

- (void) updateUI
{
    if(FBSession.activeSession.state == FBSessionStateOpen){
        [_FbButton setTitle:@"FB Logout" forState:UIControlStateNormal];
    }
    else{
        [_FbButton setTitle:@"FB Login" forState:UIControlStateNormal];
         _TextView.text = [NSString stringWithFormat:@""];
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void) facebookLogout
{
    NSLog(@"facebookLogout()");
    [FBSession.activeSession closeAndClearTokenInformation];
}

- (IBAction)onFbButtonClick:(id)sender
{
    if (FBSession.activeSession.state == FBSessionStateOpen
        || FBSession.activeSession.state == FBSessionStateOpenTokenExtended){
        // Fb Logout
        NSLog(@"onFbButtonClick() - Logout");
        [self facebookLogout];
    }else{
        // FB login
        [FBSession setActiveSession:[[FBSession alloc] initWithPermissions:[NSArray arrayWithObjects:@"public_profile,user_friends,email", nil]]];
        
        [[FBSession activeSession] openWithBehavior:FBSessionLoginBehaviorForcingWebView completionHandler:
         ^(FBSession *session,FBSessionState state,NSError *error){
            [self sessionStateChanged:session state:state error:error];
        }];    
    }
}

- (IBAction)onInstantPlayButtonClick:(id)sender {
    [gaminfinitySdk getAccountId:nil ServerUrl:GAMINFINITY_SERVER_URL];
}

- (void) onGetAccountId:(int)result AccountId:(NSString *)accountId{
    NSLog(@"DemoApp=>Result=%d, AccountId=%@", result, accountId);
    if(result == 1 ){
        _TextView.text = [NSString stringWithFormat:@"Success, AccountId=%@", accountId];
    }else{
        _TextView.text = [NSString stringWithFormat:@"Failed, ErrorCode=%d", result];
    }
}

@end
