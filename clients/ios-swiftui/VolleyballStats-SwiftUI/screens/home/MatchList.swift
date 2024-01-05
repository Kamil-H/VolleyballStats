//
//  MatchList.swift
//  VolleyballStats-SwiftUI
//
//  Created by Kamil Halko on 04/01/2024.
//

import SwiftUI
import shared

struct MatchList: View {
    
    let groupedMatchItems: [GroupedMatchItem]
    
    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: Dimens.marginMedium, pinnedViews: [.sectionHeaders]) {
                ForEach(groupedMatchItems, id: \.self) { groupedMatchItem in
                    Section {
                        ForEach(groupedMatchItem.items, id: \.self) { matchItem in
                            MatchItemView(matchItem: matchItem)
                                .padding(.horizontal, Dimens.marginMedium)
                        }
                    } header: {
                        Text(groupedMatchItem.title)
                            .font(.headline)
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .padding(.horizontal, Dimens.marginMedium)
                            .padding(.vertical, Dimens.marginSmall)
                            .background(Color.background)
                    }
                }
            }
        }
    }
}

private struct MatchItemView: View {
    
    let matchItem: MatchItem
    
    var body: some View {
        VStack(spacing: Dimens.marginSmall) {
            HStack(spacing: Dimens.marginSmall) {
                SideView(sideDetails: matchItem.left)
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                Text(matchItem.centerText)
                    .font(.title)
                    .fontWeight(.bold)
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                SideView(sideDetails: matchItem.right)
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
            }
            if let bottomText = matchItem.bottomText {
                TextPairView(textPair: bottomText)
            }
        }
        .frame(maxWidth: .infinity)
        .padding()
        .background(Color.surface)
        .cornerRadius(Dimens.cornerMedium)
        .shadow(radius: 2)
    }
}

private struct SideView: View {
    
    let sideDetails: MatchItem.SideDetails
    
    var body: some View {
        VStack(spacing: Dimens.marginSmall) {
            AsyncImage(url: URL(string: sideDetails.imageUrl as! String), scale: 2)
                .aspectRatio(contentMode: .fill)
                .frame(alignment: .center)
            Text(sideDetails.label)
                .font(.body)
                .multilineTextAlignment(.center)
                .frame(alignment: .center)
            Spacer(minLength: 0)
        }
    }
}

private struct TextPairView: View {
    
    let textPair: TextPair
    
    var body: some View {
        HStack(spacing: 0) {
            Text(textPair.first)
                .font(.footnote)
                .fontWeight(.bold)
            Text(textPair.spacer)
                .font(.footnote)
                .fontWeight(.bold)
            Text(textPair.second)
                .font(.footnote)
        }
    }
}

struct MatchList_Previews: PreviewProvider {
    
    static var previews: some View {
        MatchList(groupedMatchItems: previewData)
    }
}

private let previewData = [
    GroupedMatchItem(
        title: "2023-10-20",
        items: [
            MatchItem(
                id: 1102769,
                left: MatchItem.SideDetails(
                    label: "PSG Stal Nysa",
                    imageUrl: "https://dl.siatkarskaliga.pl/498384/inline/scalecrop=100x100/a9972e/stal_logo.png"
                ),
                right: MatchItem.SideDetails(
                    label: "Jastrzębski Węgiel",
                    imageUrl: "https://dl.siatkarskaliga.pl/498380/inline/scalecrop=100x100/a10c1c/2017_jastrzebski.png"
                ),
                centerText: "17:30",
                bottomText: nil
            )
        ]
    ),
    GroupedMatchItem(
        title: "2023-10-21",
        items: [
            MatchItem(
                id: 1102768,
                left: MatchItem.SideDetails(
                    label: "Grupa Azoty ZAKSA Kędzierzyn-Koźle",
                    imageUrl: "https://dl.siatkarskaliga.pl/498378/inline/scalecrop=100x100/d2e037/zaksa.png"
                ),
                right: MatchItem.SideDetails(
                    label: "PGE GiEK Skra Bełchatów",
                    imageUrl: "https://dl.siatkarskaliga.pl/498382/inline/scalecrop=100x100/094003/skra.png"
                ),
                centerText: "3 - 1",
                bottomText: TextPair(
                    first: "MVP",
                    second: "Łukasz Kaczmarek",
                    spacer: ": "
                )
            )
        ]
    )
]
