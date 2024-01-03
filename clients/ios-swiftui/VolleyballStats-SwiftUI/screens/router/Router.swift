//
//  Router.swift
//  VolleyballStats-SwiftUI
//
//  Created by Kamil Halko on 29/12/2023.
//

import Foundation
import SwiftUI
import shared

class Router: ObservableObject {

    @Published var path: NavigationPath = NavigationPath()
    
    private let presentersFactory: PresentersFactory
    
    init(presentersFactory: PresentersFactory) {
        self.presentersFactory = presentersFactory
    }
    
    @ViewBuilder func view(for route: Route) -> some View {
        switch onEnum(of: route.target) {
        case .filters(let filters):
            FiltersScreen(
                presentersFactory: presentersFactory,
                screen: ScreenFilters(skill: filters.skill, type: filters.type)
            )
        case .root:
            Text("error!")
        }
    }
    
    func navigateTo(_ appRoute: Route) {
        path.append(appRoute)
    }
    
    func navigateBack() {
        path.removeLast()
    }
    
    func popToRoot() {
        path.removeLast(path.count)
    }
}

// Wrapper class to implement Hashable protocol as Kotlin's sealed interface cannot implement a protocol in the Swift code
struct Route : Hashable {
    
    let target: ScreenFull
    
    init(target: ScreenFull) {
        self.target = target
    }
    
    public func hash(into hasher: inout Hasher) {
        return hasher.combine(onEnum(of: target))
    }
    
    static func == (lhs: Route, rhs: Route) -> Bool {
        return onEnum(of: lhs.target) == onEnum(of: rhs.target)
    }
}
