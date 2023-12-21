//
//  ContentView.swift
//  VolleyballStats-SwiftUI
//
//  Created by Kamil Halko on 19/12/2023.
//

import SwiftUI

struct ContentView: View {
    
    @ObservedObject private var mainViewModel: MainViewModel
    
    init(mainViewModel: MainViewModel) {
        self.mainViewModel = mainViewModel
    }
    
    var body: some View {
        VStack {
            Image(systemName: "globe")
                .imageScale(.large)
                .foregroundStyle(.tint)
            Text(mainViewModel.message)
            Button("Click me!") {
                let number = Int.random(in: Range(uncheckedBounds: (0, 100)))
                mainViewModel.onNewMessage(message: "\(mainViewModel.message), \(number)")
            }
        }
        .padding()
    }
}

@MainActor class MainViewModel: ObservableObject {
    
    @Published
    private(set) var message: String = "Start message"
    
    func onNewMessage(message: String) {
        self.message = message
    }
}
