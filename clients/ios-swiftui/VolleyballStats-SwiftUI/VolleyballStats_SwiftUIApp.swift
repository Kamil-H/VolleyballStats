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
        let mainPresenter: MainPresenter = appModule.createMainPresenter()
        print(mainPresenter.state.value)
        return true
    }
}

@main
struct VolleyballStats_SwiftUIApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    
    var body: some Scene {
        WindowGroup {
            ContentView(mainViewModel: MainViewModel())
        }
    }
}
