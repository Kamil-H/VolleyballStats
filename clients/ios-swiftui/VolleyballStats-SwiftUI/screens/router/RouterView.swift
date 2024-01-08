//
//  Router.swift
//  VolleyballStats-SwiftUI
//
//  Created by Kamil Halko on 29/12/2023.
//

import Foundation
import SwiftUI
import shared

struct RouterView<Content: View>: View {
    @StateObject var router: Router
    
    private let navigationEventReceiver: NavigationEventReceiver
    private let content: Content
    
    init(
        presentersFactory: PresentersFactory,
        navigationEventReceiver: NavigationEventReceiver,
        @ViewBuilder content: @escaping () -> Content
    ) {
        self.navigationEventReceiver = navigationEventReceiver
        _router = StateObject(wrappedValue: Router(presentersFactory: presentersFactory))
        self.content = content()
    }
    
    var body: some View {
        NavigationStack(path: $router.path) {
            content.navigationDestination(for: Route.self) { route in
                router.view(for: route)
            }
        }
        .task {
            for await navigationEvent in navigationEventReceiver.receive() {
                switch onEnum(of: navigationEvent) {
                case .close(_):
                    router.navigateBack()
                case .goTo(let goTo):
                    switch onEnum(of: goTo.screen) {
                    case .full(let fullScreen):
                        router.navigateTo(Route(target: fullScreen))
                    case .tab(_), .main(_):
                        // do nothing - it's handled by MainView
                        break
                    }
                }
            }
        }
    }
}
