//
//  ViewController.h
//  GaminfinitySDKDemo
//
//  Created by funtown on 2014/8/12.
//  Copyright (c) 2014年 Gaminfinity. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "GaminfinitySDK/GaminfinitySDK.h"
#import <FacebookSDK/FacebookSDK.h>

@interface ViewController : UIViewController <EventHandler>
- (IBAction)onInstantPlayButtonClick:(id)sender;
- (IBAction)onFbButtonClick:(id)sender;

@property (weak, nonatomic) IBOutlet UIButton *FbButton;


@property (weak, nonatomic) IBOutlet UILabel *TextView;

@end
