//
//  ContentView.swift
//  VolleyballStats-SwiftUI
//
//  Created by Kamil Halko on 19/12/2023.
//

import SwiftUI
import shared

struct MainView: View {
    
    @ObservedObject private var mainViewModel: MainViewModel
    
    @State var selectedTab: BottomMenuItem = .home
    
    init(mainViewModel: MainViewModel) {
        self.mainViewModel = mainViewModel
    }
    
    var body: some View {
        TabView(selection: $selectedTab) {
            ForEach(mainViewModel.state.bottomItems) { item in
                Tab(item: item)
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
    }
}

private struct Tab: View {
    
    private let item: BottomItemState
    
    init(item: BottomItemState) {
        self.item = item
    }
    
    var body: some View {
        if item.id == .home {
            HomeScreen()
        } else if item.id == .players {
            PlayersScreen()
        } else {
            TeamsScreen()
        }
    }
}

extension BottomItemState : Identifiable {
}
