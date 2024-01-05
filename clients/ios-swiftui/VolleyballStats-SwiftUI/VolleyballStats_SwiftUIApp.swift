//
//  VolleyballStats_SwiftUIApp.swift
//  VolleyballStats-SwiftUI
//
//  Created by Kamil Halko on 19/12/2023.
//

import SwiftUI
import shared

private let appModule = AppModule.companion.instance

class AppDelegate: UIResponder, UIApplicationDelegate {

    func application(
        _: UIApplication,
        didFinishLaunchingWithOptions _: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        appModule.appInitializer.initialize()
        return true
    }
}

@main
struct VolleyballStats_SwiftUIApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    
    var body: some Scene {
        WindowGroup {
            RouterView(
                presentersFactory: appModule.presentersFactory,
                navigationEventReceiver: appModule.navigationEventReceiver
            ) {
                MainView(presentersFactory: appModule.presentersFactory)
            }.accentColor(Color.primary)
        }
    }
}
