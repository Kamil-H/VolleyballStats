//
//  ContentView.swift
//  VolleyballStats-SwiftUI
//
//  Created by Kamil Halko on 19/12/2023.
//

import SwiftUI
import shared

struct MainView: View {
    
    private let scope: Scope
    @ObservedObject private var mainViewModel: MainViewModel
    private let presentersFactory: PresentersFactory
    
    @State var selectedTab: BottomMenuItem = .home
    
    init(presentersFactory: PresentersFactory) {
        self.presentersFactory = presentersFactory
        self.scope = presentersFactory.createScope()
        let mainPresenter = presentersFactory.createMainPresenter(scope: scope)
        self.mainViewModel = MainViewModel(mainPresenter: mainPresenter)
    }
    
    var body: some View {
        TabView(selection: $selectedTab) {
            ForEach(mainViewModel.state.bottomItems) { item in
                Tab(item: item, presentersFactory: presentersFactory)
                    .tabItem {
                        Label(item.label, systemImage: getIconName(icon: item.icon))
                    }
            }
        }
        .onChange(of: selectedTab, { oldValue, newValue in
            mainViewModel.state.bottomItems.first { item in
                item.id == newValue
            }?.onClicked(newValue)
        })
        .task { await mainViewModel.activate() }
        .onDisappear { self.scope.cancel() }
    }
}

private struct Tab: View {
    
    private let item: BottomItemState
    private let presentersFactory: PresentersFactory
    
    init(item: BottomItemState, presentersFactory: PresentersFactory) {
        self.item = item
        self.presentersFactory = presentersFactory
    }
    
    var body: some View {
        switch item.id {
        case .home:
            HomeScreen(presentersFactory: presentersFactory)
        case .players:
            StatsScreen(presentersFactory: presentersFactory, screen: ScreenStats(type: .player))
        case .teams:
            StatsScreen(presentersFactory: presentersFactory, screen: ScreenStats(type: .team))
        }
    }
}

extension BottomItemState : Identifiable {
}
